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
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class YangFoldingManager extends FoldingBuilderEx implements DumbAware {

    @Override
    public FoldingDescriptor @NotNull [] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        // Initialize the list of folding regions
        List<FoldingDescriptor> descriptors = new ArrayList<>();
        // Get a collection of the literal expressions in the document below root
        var allElementsInFile =
                new ArrayList<>(PsiTreeUtil.findChildrenOfType(root, ASTWrapperPsiElement.class));
        allElementsInFile.stream()
                .filter(this::endsWithRightBrace)
                .forEach(e -> addElementToDescriptors(descriptors, e));
        return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
    }

    @Override
    public @Nullable String getPlaceholderText(@NotNull ASTNode astNode) {
        return "{ ... }";
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode astNode) {
        return false;
    }

    private boolean endsWithRightBrace(ASTWrapperPsiElement element) {
        return element.getNode().getLastChildNode() != null &&
                element.getNode().getLastChildNode().getElementType()
                        .equals(YangTypes.YANG_RIGHT_BRACE);
    }

    private void addElementToDescriptors(List<FoldingDescriptor> descriptors, ASTWrapperPsiElement element) {
        descriptors.add(new FoldingDescriptor(element.getNode(),
                new TextRange(getLeftBracesElement(element)
                        .getTextOffset(),
                        element.getTextRange().getEndOffset()),
                FoldingGroup.newGroup("{}")));
    }

    @NotNull
    private PsiElement getLeftBracesElement(ASTWrapperPsiElement element) {
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
