package mtaa.java.data;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Application implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String description;
    private Boolean response;
    private LocalDateTime created;
    private LocalDateTime expires;
}
