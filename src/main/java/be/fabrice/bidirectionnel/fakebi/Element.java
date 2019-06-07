package be.fabrice.bidirectionnel.fakebi;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Element {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    private History currentState;
    @OneToMany(mappedBy = "element", cascade = CascadeType.ALL)
    private List<History> histories;

    public History add(Long value) {
        if(histories == null) {
            histories = new ArrayList<>();
        }

        History history = new History(this, value);

        histories.add(history);

        return history;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public History getCurrentState() {
        return currentState;
    }

    public void setCurrentState(History currentState) {
        this.currentState = currentState;
    }

    public List<History> getHistories() {
        return histories;
    }
}
