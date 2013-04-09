package com.jambit.jambel.hub.lights;

import com.google.common.collect.ImmutableSet;
import com.jambit.jambel.hub.jobs.JobState;
import org.springframework.stereotype.Component;

import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

@Component
public class ResultAggregator {

	public JobState.Result aggregate(Iterable<JobState.Result> results) {
		Set<JobState.Result> resultSet = ImmutableSet.copyOf(results);
		checkArgument(!resultSet.isEmpty(), "there is no result");

		if (resultSet.contains(JobState.Result.FAILURE))
			return JobState.Result.FAILURE;
		if (resultSet.contains(JobState.Result.UNSTABLE))
			return JobState.Result.UNSTABLE;
		if (resultSet.contains(JobState.Result.SUCCESS))
			return JobState.Result.SUCCESS;

		if (resultSet.contains(JobState.Result.ABORTED))
			return JobState.Result.ABORTED;
		if (resultSet.contains(JobState.Result.NOT_BUILT))
			return JobState.Result.NOT_BUILT;

		throw new RuntimeException("unknown results : " + resultSet);
	}

}
