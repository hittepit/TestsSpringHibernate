package be.fabrice.bidirectionnel.fakebi;

import javax.persistence.*;

@Entity
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @ManyToOne
    @JoinColumn(nullable = false)
    private Element element;
    private Long value;

    public History(Element element, Long value) {
        this.element = element;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public Element getElement() {
        return element;
    }

    public Long getValue() {
        return value;
    }
}
