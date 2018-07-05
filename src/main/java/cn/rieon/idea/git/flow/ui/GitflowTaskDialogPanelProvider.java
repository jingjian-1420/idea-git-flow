package cn.rieon.idea.git.flow.ui;

import cn.rieon.idea.git.flow.GitflowBranchUtil;
import cn.rieon.idea.git.flow.GitflowBranchUtilManager;
import com.intellij.openapi.project.Project;
import com.intellij.tasks.LocalTask;
import com.intellij.tasks.Task;
import com.intellij.tasks.TaskManager;
import com.intellij.tasks.ui.TaskDialogPanel;
import com.intellij.tasks.ui.TaskDialogPanelProvider;
import git4idea.branch.GitBranchUtil;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GitflowTaskDialogPanelProvider extends TaskDialogPanelProvider {

  @Nullable
  @Override
  public TaskDialogPanel getOpenTaskPanel(@NotNull Project project, @NotNull Task task) {
    GitRepository currentRepo = GitBranchUtil.getCurrentRepository(project);
    GitflowBranchUtil branchUtil = GitflowBranchUtilManager.getBranchUtil(currentRepo);
    if (branchUtil.hasGitflow()) {
      return TaskManager.getManager(project).isVcsEnabled() ? new GitflowOpenTaskPanel(project,
          task, currentRepo) : null;
    } else {
      return null;
    }
  }

  @Nullable
  @Override
  public TaskDialogPanel getCloseTaskPanel(@NotNull Project project, @NotNull LocalTask task) {
    GitRepository currentRepo = GitBranchUtil.getCurrentRepository(project);
    GitflowBranchUtil branchUtil = GitflowBranchUtilManager.getBranchUtil(currentRepo);

    if (branchUtil.hasGitflow()) {
      return TaskManager.getManager(project).isVcsEnabled() ? new GitflowCloseTaskPanel(project,
          task, currentRepo) : null;
    } else {
      return null;
    }
  }

}
