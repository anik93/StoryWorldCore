package com.storyworld.conditions;

import java.util.Optional;
import java.util.function.BiPredicate;

public class CommonPredicate {

	private CommonPredicate() {

	}

	public static final BiPredicate<Integer, Integer> validatePageAndPageSize = (page,
			pageSize) -> Optional.ofNullable(page).isPresent() && Optional.ofNullable(pageSize).isPresent() && page > -1
					&& pageSize > 0;

}
