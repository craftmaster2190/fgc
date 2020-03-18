package com.craftmaster.lds.fgc.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.UUID;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Entity
@Data
@Accessors(chain = true)
@Table(name = "appuser")
@Slf4j
public class UserProfile implements Serializable {
  private static final long serialVersionUID = 20200317L;
  @Id private UUID id;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @JsonIgnore
  @Lob
  @Column(columnDefinition = "BYTEA")
  private byte[] profileImage;

  private void readObject(ObjectInputStream objectInputStream)
      throws ClassNotFoundException, IOException {
    id = (UUID) objectInputStream.readObject();
  }

  private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
    objectOutputStream.writeObject(id);
  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
