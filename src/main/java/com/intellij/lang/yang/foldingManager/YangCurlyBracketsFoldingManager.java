package com.intellij.lang.yang.foldingManager;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.lang.yang.psi.YangTypes;
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

public class YangCurlyBracketsFoldingManager extends FoldingBuilderEx implements DumbAware {

    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull final PsiElement root, @NotNull final Document document, final boolean quick) {
        // Initialize the list of folding regions
        final List<FoldingDescriptor> descriptors = new ArrayList<>();
        // Get a collection of the literal expressions in the document below root
        final var allElementsInFile =
                new ArrayList<>(PsiTreeUtil.findChildrenOfType(root, ASTWrapperPsiElement.class));
        allElementsInFile.stream()
                .filter(this::endsWithRightBrace)
                .forEach(e -> this.addElementToDescriptors(descriptors, e));
        return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull final ASTNode astNode) {
        return "{ ... }";
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull final ASTNode astNode) {
        return false;
    }

    private boolean endsWithRightBrace(final ASTWrapperPsiElement element) {
        return element.getNode().getLastChildNode() != null &&
                element.getNode().getLastChildNode().getElementType()
                        .equals(YangTypes.YANG_RIGHT_BRACE);
    }

    public void addElementToDescriptors(final List<FoldingDescriptor> descriptors, final ASTWrapperPsiElement element) {
        // error in element (ends with YANG_RIGHT_BRACE but does not include YANG_LEFT_BRACE
        if (element == null) return;
        descriptors.add(new FoldingDescriptor(element.getNode(),
                new TextRange(this.getLeftBracesElement(element)
                        .getTextOffset(),
                        element.getTextRange().getEndOffset()),
                FoldingGroup.newGroup("desc")));
    }

    private PsiElement getLeftBracesElement(final ASTWrapperPsiElement element) {
        var child = element.getFirstChild();
        while (child != null) {
            if (child.getNode().getElementType().equals(YangTypes.YANG_LEFT_BRACE)) {
                return child;
            }
            child = PsiTreeUtil.nextLeaf(child);
        }
        return child;
    }
}
