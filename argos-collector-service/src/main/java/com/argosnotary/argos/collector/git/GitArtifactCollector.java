/*
 * Copyright (C) 2020 Argos Notary Coöperatie UA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.argosnotary.argos.collector.git;

import com.argosnotary.argos.collector.ArtifactCollectorException;
import com.argosnotary.argos.collector.ArtifactCollectorProvider;
import com.argosnotary.argos.collector.rest.api.model.Artifact;
import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.dfs.DfsRepositoryDescription;
import org.eclipse.jgit.internal.storage.dfs.InMemoryRepository;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.TagOpt;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Component
@Profile(GitArtifactCollector.GIT)
public class GitArtifactCollector implements ArtifactCollectorProvider<GitSpecificationAdapter> {
    public static final String DEFAULT_EXCLUDE_PATTERNS = "{**.git/**,**.git\\\\**}";

    static final String GIT = "GIT";

    @Value("${argos-collector.collectortypes.git.baseurl}")
    private String gitBaseUrl;

    @Override
    public List<Artifact> collectArtifacts(GitSpecificationAdapter spec) {

        PathMatcher excludeMatcher = FileSystems.getDefault().getPathMatcher("glob:" + DEFAULT_EXCLUDE_PATTERNS);

        List<Artifact> artifacts = new ArrayList<>();
        InMemoryRepository repo = getInMemoryRepository(spec);
        RevCommit commit = getRevCommit(spec, repo);
        try (TreeWalk treeWalk = new TreeWalk(repo)) {
            treeWalk.addTree(commit.getTree());
            treeWalk.setRecursive(true);
            while (treeWalk.next()) {
                if (!excludeMatcher.matches(Path.of(treeWalk.getPathString()))) {
                    try (InputStream fileStream = repo.open(treeWalk.getObjectId(0)).openStream()) {
                        artifacts.add(new Artifact()
                                .uri(treeWalk.getPathString())
                                .hash(DigestUtils.sha256Hex(fileStream)));
                    }
                }
            }
        } catch (IOException e) {
            throw new ArtifactCollectorException(e.getMessage(), e);
        }
        return artifacts;
    }

    private RevCommit getRevCommit(GitSpecificationAdapter spec, InMemoryRepository repo) {
        try (RevWalk revWalk = new RevWalk(repo)) {
            ObjectId lastCommitId = getObjectId(spec, repo);
            return revWalk.parseCommit(lastCommitId);
        } catch (IOException e) {
            throw new ArtifactCollectorException(e.getMessage(), e);
        }
    }

    private ObjectId getObjectId(GitSpecificationAdapter spec, InMemoryRepository repo) {
        Optional<String> optionalBranch = Optional.ofNullable(spec.getBranch());
        Optional<String> optionalTag = Optional.ofNullable(spec.getTag());
        Optional<String> optionalCommitHash = Optional.ofNullable(spec.getCommitHash());

        return optionalCommitHash.map(hash -> resolve(repo, hash))
                .orElseGet(() -> optionalTag.map(tag -> resolve(repo, "refs/tags/" + tag))
                        .orElseGet(() -> optionalBranch.map(branch -> resolve(repo, "refs/heads/" + branch))
                                .orElseThrow(() -> new ArtifactCollectorException("no commitHash or tag or branch specified")))
                );
    }

    private ObjectId resolve(InMemoryRepository repo, String revstr) {
        try {
            return Optional.ofNullable(repo.resolve(revstr)).orElseThrow(() -> new ArtifactCollectorException(revstr + " not found"));
        } catch (IOException e) {
            throw new ArtifactCollectorException(e.getMessage(), e);
        }
    }

    private InMemoryRepository getInMemoryRepository(GitSpecificationAdapter spec) {

        String repoUrl = fromHttpUrl(gitBaseUrl).path("/").path(spec.getRepository()).build().toUriString();
        DfsRepositoryDescription repoDesc = new DfsRepositoryDescription();
        InMemoryRepository repo = new InMemoryRepository(repoDesc);
        try (Git git = new Git(repo)) {
            git.fetch().setCredentialsProvider(getCredentialsProvider(spec))
                    .setRemote(repoUrl)
                    .setRefSpecs(new RefSpec("+refs/heads/*:refs/heads/*"))
                    .setTagOpt(Optional.ofNullable(spec.getTag()).map(tag -> TagOpt.FETCH_TAGS).orElse(TagOpt.NO_TAGS))
                    .call();
        } catch (GitAPIException e) {
            throw new ArtifactCollectorException(e.getMessage(), e);
        }
        return repo;
    }

    private CredentialsProvider getCredentialsProvider(GitSpecificationAdapter spec) {
        if (!Objects.isNull(spec.getUsername()) && !Objects.isNull(spec.getPassword())) {
            return new UsernamePasswordCredentialsProvider(spec.getUsername(), spec.getPassword());
        } else {
            return null;
        }

    }

}
