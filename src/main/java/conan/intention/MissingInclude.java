package conan.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VirtualFileImpl;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.PsiManagerImpl;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.cidr.lang.psi.impl.OCIncludeDirectiveImpl;
import conan.ui.ConanConfirmDialog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;


public class MissingInclude extends PsiElementBaseIntentionAction implements IntentionAction {

    @NotNull
    public String getText() {
        return "Conan: Search package in conan-center";
    }

    @NotNull
    public String getFamilyName() {
        return "ConanMissingPackage";
    }

    public boolean isAvailable(@NotNull Project project, Editor editor, @Nullable PsiElement element) {
        if (element == null){
            return false;
        }
        IElementType type = ((LeafPsiElement) element.getParent().getLastChild()).getElementType();
        if (type.toString().equals("HEADER_PATH_LITERAL")){
            PsiElement include = element.getParent();
            // Broken link
            return ((OCIncludeDirectiveImpl) include).getIncludedFile() == null;
        }
        return false;
    }

    public void invoke(@NotNull Project project, Editor editor, PsiElement element) throws IncorrectOperationException {
        String include_text = element.getParent().getLastChild().getText();
        if (isConanfileTXT(project)) {
            boolean result = new ConanConfirmDialog("Search header '" + include_text + "'",
                    "Select reference to add to 'conanfile.txt': 'zlib/1.2.11@conan/stable'").showAndGet();
        }
        else{
            boolean result = new ConanConfirmDialog("Search header '" + include_text + "'",
                    "Copy reference to add it to 'conanfile.py': 'zlib/1.2.11@conan/stable'").showAndGet();

        }
    }

    private boolean isConanfileTXT(Project project){
        Path conanTxtFile = Paths.get(project.getBasePath(), "conanfile.txt");
        return conanTxtFile.toFile().exists();
    }

    /**
     * Indicates this intention action expects the Psi framework to provide the write action
     * context for any changes.
     *
     * @return <ul>
     * <li> true if the intention requires a write action context to be provided</li>
     * <li> false if this intention action will start a write action</li>
     * </ul>
     */
    public boolean startInWriteAction() {return true;}
}
