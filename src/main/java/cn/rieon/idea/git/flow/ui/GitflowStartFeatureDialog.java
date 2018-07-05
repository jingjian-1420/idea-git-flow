package cn.rieon.idea.git.flow.ui;

import cn.rieon.idea.git.flow.GitflowConfigUtil;
import com.intellij.openapi.project.Project;
import git4idea.repo.GitRepository;

public class GitflowStartFeatureDialog extends AbstractBranchStartDialog {

  public GitflowStartFeatureDialog(Project project, GitRepository repo) {
    super(project, repo);
  }

  @Override
  protected String getLabel() {
    return "feature";
  }

  @Override
  protected String getDefaultBranch() {
    return GitflowConfigUtil.getDevelopBranch(getProject(), myRepo);
  }
}
