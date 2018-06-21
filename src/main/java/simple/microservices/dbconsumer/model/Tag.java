package simple.microservices.dbconsumer.model;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tag")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tag {

    @Id
    @Column(name = "name")
    private String name;

    @ManyToMany(fetch = FetchType.EAGER,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE,
                    CascadeType.REFRESH,
                    CascadeType.DETACH,
            })
    @JoinTable(name = "tag_poem",
            inverseJoinColumns = {@JoinColumn(name = "poem_id", referencedColumnName = "id")},
            joinColumns = {@JoinColumn(name = "tag_name", referencedColumnName = "name")})
    @org.springframework.data.annotation.Transient
    private Set<Poem> poems;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return
                Objects.equals(name, tag.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Tag{" +
                "name='" + name + '\'' +
                '}';
    }
}
