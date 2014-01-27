package de.tum.pssif.transform.transformation;

import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.Metamodel;


public class HideEdgeTypeTransformation extends HideTypeTransformation<EdgeType> {
  public HideEdgeTypeTransformation(EdgeType type) {
    super(type);
  }

  @Override
  public Metamodel apply(View view) {
    if (getType() != null) {
      removeType(view, getType());
    }
    return view;
  }

  private void removeType(View view, EdgeType type) {
    if (view.findEdgeType(type.getName()) != null) {
      for (EdgeType special : type.getSpecials()) {
        removeType(view, special);
      }
      //TODO
      view.removeEdgeType(view.findEdgeType(type.getName()));
    }
  }
}
