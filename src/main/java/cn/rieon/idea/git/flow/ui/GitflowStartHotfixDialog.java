package cn.rieon.idea.git.flow.ui;

import cn.rieon.idea.git.flow.GitflowConfigUtil;
import com.intellij.openapi.project.Project;
import git4idea.repo.GitRepository;

public class GitflowStartHotfixDialog extends AbstractBranchStartDialog {

  public GitflowStartHotfixDialog(Project project, GitRepository repo) {
    super(project, repo);
  }

  @Override
  protected String getLabel() {
    return "hotfix";
  }

  @Override
  protected String getDefaultBranch() {
    return GitflowConfigUtil.getMasterBranch(getProject(), myRepo);
  }
}
