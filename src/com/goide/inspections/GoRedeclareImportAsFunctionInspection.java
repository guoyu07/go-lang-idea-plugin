/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Florin Patan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.goide.inspections;

import com.goide.psi.GoFile;
import com.goide.psi.GoFunctionDeclaration;
import com.goide.psi.GoImportSpec;
import com.goide.runconfig.testing.GoTestFinder;
import com.intellij.codeInspection.LocalQuickFixBase;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GoRedeclareImportAsFunctionInspection extends GoInspectionBase {
  @Override
  protected void checkFile(@NotNull GoFile file, @NotNull ProblemsHolder problemsHolder) {
    if (GoTestFinder.getTestTargetPackage(file) != null) return;

    List<GoFunctionDeclaration> functions = file.getFunctions();
    String funcName;

    for (GoFunctionDeclaration function : functions) {
      funcName = function.getName();
      if (funcName == null) continue;
      for (GoImportSpec importSpec : file.getImports()) {
        if (funcName.equals(importSpec.getLocalPackageName())) {
          problemsHolder.registerProblem(function.getIdentifier(), getMessage(importSpec.getLocalPackageName()), new GoRenameQuickFix(function));
        }
      }
    }
  }

  private static String getMessage(String name) {
    return "import \"" + name + "\" redeclared in this block";
  }
}