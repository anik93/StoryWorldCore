package com.storyworld.domain.sql;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.storyworld.domain.sql.basic.BasicEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "STORYRULE")
public class StoryRule extends BasicEntity {

	private String script;

}
