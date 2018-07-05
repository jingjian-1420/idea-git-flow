package cn.rieon.idea.git.flow.actions;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import git4idea.commands.GitLineHandlerListener;
import java.util.ArrayList;
import org.apache.commons.lang.StringUtils;

//generic line handler (should handle errors etc)
public abstract class GitflowLineHandler implements GitLineHandlerListener {

  ArrayList<String> myErrors = new ArrayList<String>();
  Project myProject;

  @Override
  public void onLineAvailable(String line, Key outputType) {
    if (line.contains("fatal") || line.contains("Fatal")) {
      myErrors.add(line);
    }
  }

  @Override
  public void processTerminated(int exitCode) {
  }

  @Override
  public void startFailed(Throwable exception) {
  }

  public String getErrors() {
    return StringUtils.join(myErrors, ",");
  }
}