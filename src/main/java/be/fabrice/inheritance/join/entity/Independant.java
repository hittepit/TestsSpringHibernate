package be.fabrice.inheritance.join.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import java.util.Objects;

@Entity
@Table(name="INDE")
public class Independant extends Employeur{
    private String onss;

    public String getOnss() {
        return onss;
    }

    public void setOnss(String onss) {
        this.onss = onss;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Independant)) return false;
        Independant that = (Independant) o;
        return Objects.equals(onss, that.onss);
    }

    @Override
    public int hashCode() {
        return Objects.hash(onss);
    }
}
