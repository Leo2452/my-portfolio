// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.ArrayList;

public final class FindMeetingQuery {
    public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
        Collection<TimeRange> workingTimes = new ArrayList<TimeRange>();
        int start = TimeRange.START_OF_DAY;
        int end;
        boolean foundGuest = false;

        for(Event occasion: events) {
            for(String mandatoryGuest: request.getAttendees()) {
                // Check possibilities before occupied event.
                if(occasion.getAttendees().contains(mandatoryGuest)) {
                    foundGuest = true;
                    break;
                }
            }
            if(foundGuest) {
                end = occasion.getWhen().start();
                if(end - start >= request.getDuration()) {
                    workingTimes.add(TimeRange.fromStartEnd(start, end, false));
                }
                // Change start if endpoint current person's occasion is greater
                if(occasion.getWhen().end() > start) {
                    start = occasion.getWhen().end();
                }
                foundGuest = false;
            }
        }
        workingTimes = updateWithEndOfDay(workingTimes, start, request.getDuration());
        return workingTimes;
    }

    public Collection<TimeRange> updateWithEndOfDay(Collection<TimeRange> workingTimes, int start, long meetingDuration) {
        int end = TimeRange.END_OF_DAY;
        if (end - start >= meetingDuration) {
            workingTimes.add(TimeRange.fromStartEnd(start, end, true));
        }
        return workingTimes;
    }
}
