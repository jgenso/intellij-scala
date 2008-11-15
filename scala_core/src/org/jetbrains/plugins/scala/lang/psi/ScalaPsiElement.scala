package org.jetbrains.plugins.scala.lang.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.tree.{TokenSet, IElementType}

trait ScalaPsiElement extends PsiElement {
  protected def findChildByClass[T >: Null <: ScalaPsiElement](clazz: Class[T]): T

  protected def findChildrenByClass[T >: Null <: ScalaPsiElement](clazz: Class[T]): Array[T]

  protected def findChild[T >: Null <: ScalaPsiElement](clazz: Class[T]): Option[T] = findChildByClass(clazz) match {
    case null => None
    case e => Some(e)
  }

  def findLastChildByType(t : IElementType) = {
    var node = getNode.getLastChildNode
    while(node != null && node.getElementType != t) {
      node = node.getTreePrev
    }
    if (node == null) null else node.getPsi
  }

  def findLastChildByType(set : TokenSet) = {
    var node = getNode.getLastChildNode
    while(node != null && !set.contains(node.getElementType)) {
      node = node.getTreePrev
    }
    if (node == null) null else node.getPsi
  }

  protected def findLastChild[T >: Null <: ScalaPsiElement](clazz: Class[T]): Option[T] = {
    var child = getLastChild
    while(child != null && !clazz.isInstance(child)) {
      child = child.getPrevSibling
    }
    if (child == null) None else Some(child.asInstanceOf[T])  
  }


  override def delete: Unit = {
    getParent.getNode.removeChild(getNode)
  }
}