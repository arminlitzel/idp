package de.tum.pssif.core.metamodel;

/**
 * Concept which is used in the meta-model
 * to describe attributes.
 *
 */
public interface AttributeType extends Named {

  /**
   * @return
   *    The data type of this attribute type.
   */
  DataType getType();

  /**
   * @return
   *    The units of this attribute type. Can be {@link de.tum.pssif.core.metamodel.Units.NONE}.
   */
  Unit getUnit();

  /**
   * @return
   *    Whether the attribute type is visible. This kind of visibility
   *    is concerned with UI and visualizations and does <b>not</b> 
   *    have anyting to do with permissions. 
   */
  boolean isVisible();

  /**
   * @return
   *    The category of this attribute type. See {@link AttributeCategory}.
   */
  AttributeCategory getCategory();

}
