package com.teamvectora.elixirapi.model;

import java.io.Serializable;
import java.util.Objects;

public class Inventory implements Serializable {
    private int id;
    private int characterId;
    private int itemId;
    private int typeItemId;

    public Inventory() {
    }

    public Inventory(int characterId, int itemId, int typeItemId) {
        this.characterId = characterId;
        this.itemId = itemId;
        this.typeItemId = typeItemId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCharacterId() {
        return characterId;
    }

    public void setCharacterId(int characterId) {
        this.characterId = characterId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getTypeItemId() {
        return typeItemId;
    }

    public void setTypeItemId(int typeItemId) {
        this.typeItemId = typeItemId;
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "id=" + id +
                ", characterId=" + characterId +
                ", itemId=" + itemId +
                ", typeItemId=" + typeItemId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Inventory)) return false;
        Inventory inventory = (Inventory) o;
        return getId() == inventory.getId() && getCharacterId() == inventory.getCharacterId() && getItemId() == inventory.getItemId() && getTypeItemId() == inventory.getTypeItemId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCharacterId(), getItemId(), getTypeItemId());
    }
}
