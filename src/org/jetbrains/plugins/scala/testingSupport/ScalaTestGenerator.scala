package org.jetbrains.plugins.scala
package testingSupport

import com.intellij.openapi.project.Project
import com.intellij.testIntegration.createTest.{CreateTestDialog, TestGenerator}
import com.intellij.openapi.fileEditor.ex.IdeDocumentHistory
import com.intellij.openapi.editor.Editor
import com.intellij.util.IncorrectOperationException
import actions.NewScalaTypeDefinitionAction
import com.intellij.openapi.ui.Messages
import com.intellij.codeInsight.{CodeInsightBundle, CodeInsightUtil}
import com.intellij.psi._
import search.GlobalSearchScope
import lang.psi.impl.ScalaPsiElementFactory
import extensions._
import lang.psi.api.toplevel.typedef.ScTypeDefinition
import lang.psi.api.base.ScStableCodeReferenceElement


class ScalaTestGenerator extends TestGenerator {
  def generateTest(project: Project, d: CreateTestDialog): PsiElement = {
    return postponeFormattingWithin(project) {
      inWriteAction {
        try {
          val file: PsiFile = generateTestInternal(project, d)
          file
        } catch {
          case e: IncorrectOperationException =>
            invokeLater {
              val message = CodeInsightBundle.message("intention.error.cannot.create.class.message", d.getClassName)
              val title = CodeInsightBundle.message("intention.error.cannot.create.class.title")
              Messages.showErrorDialog(project, message, title)
            }
            null
        }
      }
    }
  }

  override def toString: String = ScalaFileType.SCALA_LANGUAGE.getDisplayName

  private def generateTestInternal(project: Project, d: CreateTestDialog): PsiFile = {
    IdeDocumentHistory.getInstance(project).includeCurrentPlaceAsChangePlace()
    val SCALA_EXTENSIOIN = "." + ScalaFileType.DEFAULT_EXTENSION
    val file = NewScalaTypeDefinitionAction.createFromTemplate(d.getTargetDirectory, d.getClassName, d.getClassName + SCALA_EXTENSIOIN, "Scala Class")
    val typeDefinition = file.depthFirst.filterByType(classOf[ScTypeDefinition]).next()
    val scope: GlobalSearchScope = GlobalSearchScope.allScope(project)
    val fqName = d.getSuperClassName
    if (fqName != null) {
      val psiClass: Option[PsiClass] = Option(JavaPsiFacade.getInstance(project).findClass(fqName, scope))
      addSuperClass(typeDefinition, psiClass, fqName)
    }
    val positionElement = typeDefinition.extendsBlock.templateBody.map(_.getFirstChild).getOrElse(typeDefinition)
    var editor: Editor = CodeInsightUtil.positionCursor(project, file, positionElement)
    // TODO add test methods?
    // addTestMethods(editor, targetClass, d.getSelectedTestFrameworkDescriptor, d.getSelectedMethods, d.shouldGeneratedBefore, d.shouldGeneratedAfter)
    file
  }

  private def addSuperClass(typeDefinition: ScTypeDefinition, psiClass: Option[PsiClass], fqName: String) = {
    val extendsBlock = typeDefinition.extendsBlock
    def addExtendsRef(refName: String) = {
      val (extendsToken, classParents) = ScalaPsiElementFactory.createClassTemplateParents(refName, typeDefinition.getManager)
      val extendsAdded = extendsBlock.addBefore(extendsToken, extendsBlock.getFirstChild)
      extendsBlock.addAfter(classParents, extendsAdded)
    }
    psiClass match {
      case Some(cls) =>
        val classParents = addExtendsRef(cls.getName)
        classParents.depthFirst.filterByType(classOf[ScStableCodeReferenceElement]).next().bindToElement(cls)
      case None =>
        addExtendsRef(fqName)
    }
  }
}