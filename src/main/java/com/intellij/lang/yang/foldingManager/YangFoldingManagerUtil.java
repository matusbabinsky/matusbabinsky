package com.intellij.lang.yang.foldingManager;

import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.PsiErrorElementImpl;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

public class YangFoldingManagerUtil {

    private YangFoldingManagerUtil() {
    }

    public static boolean isFileValid(@NotNull PsiElement root) {
        return PsiTreeUtil.findChildrenOfType(root, PsiErrorElementImpl.class).size() == 0;
    }
}
