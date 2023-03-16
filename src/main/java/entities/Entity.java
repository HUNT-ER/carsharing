package entities;

import java.util.Objects;

public abstract class Entity {

  protected int id;
  protected String name;

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Entity)) {
      return false;
    }
    Entity entity = (Entity) o;
    return id == entity.id && name.equals(entity.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name);
  }
}
