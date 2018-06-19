package simple.microservices.dbconsumer.model;

import lombok.*;

import javax.xml.bind.annotation.XmlRootElement;
import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tag")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Tag {

    @Id
    private Integer id;

    @Column(name = "name", unique = true)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE,
                    CascadeType.REFRESH,
                    CascadeType.DETACH
            },
            mappedBy = "tags")

    private Set<Poem> poems;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(id, tag.id) &&
                Objects.equals(name, tag.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name);
    }
}
