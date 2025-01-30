package com.inovationware.toolkit.global.library.external;

import android.os.Build;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kohsuke.github.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;


/**
 * Client to work with GitHub.
 * Paths are case-sensitive, unless stated otherwise.
 * Other string parameters may be case-sensitive.
 */
public class GitHubLite {

    public enum Branch {
        master,
        main
    }

    private final String token;
    private final String owner;
    private String repository;
    private String branch;
    private GitHub github;

    private GitHubLite(String owner, String repository, String token, String branch) throws IOException {
        this.owner = owner;
        this.repository = repository;
        this.token = token;
        this.branch = branch;
        initialize();
    }

    private void initialize() throws IOException {
        github = new GitHubBuilder().withOAuthToken(token).build();
    }

    public GitHubLite setBranch(String branch) {
        this.branch = branch;
        return this;
    }

    public GitHubLite setBranch(Branch branch) {
        this.branch = branch.name();
        return this;
    }

    private String getBranch() {
        return this.branch;
    }

    private GitHubLite setRepository(String repository) {
        this.repository = repository;
        return this;
    }

    private String getOwner() {
        return this.owner;
    }

    private String getRepository() {
        return this.repository;
    }


    /**
     * Creates client to work with GitHub.
     * Paths are case-sensitive, unless stated otherwise.
     * Other string parameters may be case-sensitive.
     */

    public static class builder {

        private String token;
        private String owner;
        private String repository;
        private String branch = Branch.master.name();

        public builder(@NotNull String owner, @NotNull String repository, @NotNull String token) {
            this.owner = owner;
            this.repository = repository;
            this.token = token;
        }

        public builder(@NotNull String owner, @NotNull String repository, @NotNull String token, @NotNull Branch branch) {
            this.owner = owner;
            this.repository = repository;
            this.token = token;
            this.branch = branch.name();
        }

        public builder(@NotNull String owner, @NotNull String repository, @NotNull String token, @NotNull String branch) {
            this.owner = owner;
            this.repository = repository;
            this.token = token;
            this.branch = branch;
        }

        public builder setBranch(@NotNull String branch) {
            this.branch = branch;
            return this;
        }

        public builder setBranch(@NotNull Branch branch) {
            this.branch = branch.name();
            return this;
        }

        public GitHubLite createClient() throws IOException {
            return new GitHubLite(
                    this.owner,
                    this.repository,
                    this.token,
                    this.branch
            );
        }

    }


    /**
     * Client to work with local file system.
     */

    /*
    ToDo Implement this feature
     */
    public static class FSLite {

        public void saveFile(@NotNull InputStream inputStream, @NotNull String targetFilePath) throws IOException {
            /*File targetFile = new File(targetFilePath);
            OutputStream outStream = new FileOutputStream(targetFile);
            outStream.write(inputStream.readAllBytes());

            IOUtils.closeQuietly(outStream);*/
        }

    }


    public void createRepository(@NotNull String repository, @Nullable String description, @Nullable String homepage, boolean isPublic) throws IOException {
        github.createRepository(
                repository,
                description == null ? "" : description.isEmpty() ? "" : description,
                homepage == null ? "" : homepage.isEmpty() ? "" : homepage,
                isPublic
        );
    }

    public void createRepository(@NotNull String repository, boolean isPublic) throws IOException {
        createRepository(repository, null, null, isPublic);
    }

    public String readReadMe() throws IOException {
        InputStream stream = github.getRepository(getOwner() + "/" + getRepository())
                .getReadme().read();

        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                (stream, StandardCharsets.UTF_8))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }

        return textBuilder.toString();
    }

    public void createFile(@NotNull String path, @NotNull String filePath, @NotNull String commitMessage) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                github.getRepository(getOwner() + "/" + getRepository())
                        .createContent(Files.readAllBytes(Path.of(filePath)), commitMessage, path, getBranch());
            }
        }
    }

    public void createTextFile(@NotNull String path, @NotNull String content, @NotNull String commitMessage) throws IOException {
        github.getRepository(getOwner() + "/" + getRepository())
                .createContent(content, commitMessage, path, getBranch());
    }

    public InputStream readFile(@NotNull String path) throws IOException {
        return github.getRepository(getOwner() + "/" + getRepository())
                .getFileContent(path).read();
    }

    public String readTextFile(@NotNull String path) throws IOException {

        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                (readFile(path), StandardCharsets.UTF_8))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }

        return textBuilder.toString();
    }

    public void createGist(@NotNull String path, @NotNull String content, @NotNull String description, boolean isPublic) throws IOException {
        github.createGist()
                .description(description)
                .file(path, content)
                .public_(isPublic)
                .create();
    }
}
