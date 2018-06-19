package simple.microservices.dbconsumer.model;

import lombok.*;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Set;

@XmlRootElement
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode @ToString
@Entity
@Table(name = "poem")
public class Poem {

    @Id
    private Integer id;

    @Column(name = "author")
    private String username;

    @Column(name = "text")
    private String data;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE,
                    CascadeType.REFRESH,
                    CascadeType.DETACH
            })
    @JoinTable(name = "tag_poem",
            joinColumns = {@JoinColumn(name = "poem_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id", referencedColumnName = "id")})
    private Set<Tag> tags;

    public void addTag(Tag tag){
        tags.add(tag);
    }
}
