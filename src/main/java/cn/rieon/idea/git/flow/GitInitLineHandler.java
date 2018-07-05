package cn.rieon.idea.git.flow;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.commands.GitCommand;
import git4idea.commands.GitLineHandler;
import git4idea.util.GitVcsConsoleWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class GitInitLineHandler extends GitLineHandler {

  @NotNull
  private final GitVcsConsoleWriter consoleWriter;
  GitflowInitOptions _initOptions;
  private BufferedWriter writer;

  public GitInitLineHandler(GitflowInitOptions initOptions, @NotNull Project project,
      @NotNull VirtualFile vcsRoot, @NotNull GitCommand command) {
    super(project, vcsRoot, command);
    this.consoleWriter = GitVcsConsoleWriter.getInstance(project);
    _initOptions = initOptions;
  }

  @Nullable
  @Override
  protected Process startProcess() throws ExecutionException {
    Process p = super.startProcess();
    writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
    return p;
  }

  protected void processTerminated(final int exitCode) {
    super.processTerminated(exitCode);
  }


  @Override
  protected void onTextAvailable(String s, Key key) {
    super.onTextAvailable(s, key);
    try {
      if (s.contains("name for production releases")) {
        writer.write(_initOptions.getProductionBranch());
        consoleWriter.showCommandLine(_initOptions.getProductionBranch());
        writer.newLine();
        writer.flush();
      }

      if (s.contains("name for \"next release\"")) {
        writer.write(_initOptions.getDevelopmentBranch());
        consoleWriter.showCommandLine(_initOptions.getDevelopmentBranch());
        writer.newLine();
        writer.flush();
      }

      if (s.contains("Feature branches")) {
        writer.write(_initOptions.getFeaturePrefix());
        consoleWriter.showCommandLine(_initOptions.getFeaturePrefix());
        writer.newLine();
        writer.flush();
      }
      if (s.contains("Bugfix branches")) {
        writer.write(_initOptions.getBugfixPrefix());
        consoleWriter.showCommandLine(_initOptions.getBugfixPrefix());
        writer.newLine();
        writer.flush();
      }
      if (s.contains("Release branches")) {
        writer.write(_initOptions.getReleasePrefix());
        consoleWriter.showCommandLine(_initOptions.getReleasePrefix());
        writer.newLine();
        writer.flush();
      }
      if (s.contains("Hotfix branches")) {
        writer.write(_initOptions.getHotfixPrefix());
        consoleWriter.showCommandLine(_initOptions.getHotfixPrefix());
        writer.newLine();
        writer.flush();
      }
      if (s.contains("Support branches")) {
        writer.write(_initOptions.getSupportPrefix());
        consoleWriter.showCommandLine(_initOptions.getSupportPrefix());
        writer.newLine();
        writer.flush();
      }
      if (s.contains("Version tag")) {
        writer.write(_initOptions.getVersionPrefix());
        consoleWriter.showCommandLine(_initOptions.getVersionPrefix());
        writer.newLine();
        writer.flush();
      }
      if (s.contains("Hooks and filters")) {
        writer.newLine();
        writer.flush();
      }


    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
