package org.jetbrains.plugins.scala.editor.selectioner

import com.intellij.codeInsight.editorActions.ExtendWordSelectionHandlerBase
import org.jetbrains.plugins.scala.lang.psi.api.expr.ScBlockExpr
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import org.jetbrains.plugins.scala.lang.lexer.ScalaTokenTypes
import java.util.ArrayList
import com.intellij.psi.{TokenType, PsiElement}

/**
 * @author yole
 */

class ScalaCodeBlockSelectioner extends ExtendWordSelectionHandlerBase {
  def canSelect(e: PsiElement) = e.isInstanceOf[ScBlockExpr]

  override def select(e: PsiElement, editorText: CharSequence, cursorOffset: Int, editor: Editor) = {
    var firstChild = e.getNode.getFirstChildNode
    var lastChild = e.getNode.getLastChildNode
    if (firstChild.getElementType == ScalaTokenTypes.tLBRACE && lastChild.getElementType == ScalaTokenTypes.tRBRACE) {
      while(firstChild.getTreeNext != null && firstChild.getTreeNext.getElementType == TokenType.WHITE_SPACE) {
        firstChild = firstChild.getTreeNext
      }
      while(lastChild.getTreePrev != null && lastChild.getTreePrev.getElementType == TokenType.WHITE_SPACE) {
        lastChild = lastChild.getTreePrev
      }
      val start = firstChild.getTextRange.getEndOffset
      val end = lastChild.getTextRange.getStartOffset
      ExtendWordSelectionHandlerBase.expandToWholeLine(editorText, new TextRange(start, end))
    }
    else {
      new ArrayList[TextRange]
    }
  }
}