package simple.microservices.dbconsumer.model;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode @ToString
@Entity
@Table(name = "poem")
public class Poem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "poem_gen")
    @SequenceGenerator(name = "poem_gen", sequenceName = "poem_seq")
    @org.springframework.data.annotation.Transient
    private int id;

    @Column(name = "author")
    private String username;

    @Column(name = "text")
    private String data;

    @Transient
    private Set<Tag> tags;
}
