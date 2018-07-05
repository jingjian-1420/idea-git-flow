package cn.rieon.idea.git.flow.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import org.jetbrains.annotations.Nullable;

/**
 * Dialog for choosing branches
 *
 * @author Opher Vishnia / opherv.com / opherv@gmail.com
 */

public class GitflowBranchChooseDialog extends DialogWrapper {

  private JPanel contentPane;
  private JList branchList;


  public GitflowBranchChooseDialog(Project project, List<String> branchNames) {
    super(project, true);

    setModal(true);

    setTitle("Choose Branch");
    branchList.setListData(branchNames.toArray());

    init();
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    return contentPane;
  }

  public String getSelectedBranchName() {
    return branchList.getSelectedValue().toString();
  }
}
