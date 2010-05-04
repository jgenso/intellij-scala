package org.jetbrains.plugins.scala.codeInsight.intention.types

import org.jetbrains.plugins.scala.lang.psi.api.base.patterns.{ScBindingPattern, ScTypedPattern}
import org.jetbrains.plugins.scala.lang.psi.api.statements.{ScFunctionDefinition, ScPatternDefinition, ScVariableDefinition}

/**
 * Pavel.Fatin, 28.04.2010
 */

trait Strategy {
  def addToFunction(function: ScFunctionDefinition)

  def removeFromFunction(function: ScFunctionDefinition)

  def addToValue(value: ScPatternDefinition)

  def removeFromValue(value: ScPatternDefinition)

  def addToVariable(variable: ScVariableDefinition)

  def removeFromVariable(variable: ScVariableDefinition)

  def addToPattern(pattern: ScBindingPattern)

  def removeFromPattern(pattern: ScTypedPattern)
}