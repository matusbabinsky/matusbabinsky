package com.intellij.lang.yang.foldingManager;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.lang.psi.impl.YangDescriptionStmtImpl;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class YangDescriptionFoldingManager extends FoldingBuilderEx implements DumbAware {


    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull final PsiElement root, @NotNull final Document document, final boolean b) {
        // Initialize the list of folding regions
        final List<FoldingDescriptor> descriptors = new ArrayList<>();
        // Get a collection of the all Description elements in the document below root
        final var allElementsInFile =
                new ArrayList<>(PsiTreeUtil.findChildrenOfType(root, YangDescriptionStmtImpl.class));
        allElementsInFile.stream()
                .forEach(e -> this.addElementToDescriptors(descriptors, e));
        return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
    }

    @Override
    public @Nullable
    String getPlaceholderText(@NotNull final ASTNode astNode) {
        return " \"...\"";
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull final ASTNode astNode) {
        return false;
    }

    public void addElementToDescriptors(final List<FoldingDescriptor> descriptors, final ASTWrapperPsiElement element) {
        descriptors.add(new FoldingDescriptor(element.getNode(),
                new TextRange(element
                        .getTextOffset() + element.getNode().getFirstChildNode().getText().length(),
                        element.getTextRange().getEndOffset() - 1),
                FoldingGroup.newGroup("description")));
    }
}
