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
import java.util.List;

public final class FindMeetingQuery {
    public static final int END_OF_DAY = 1440;

    public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
        Collection<TimeRange> workingTimes = new ArrayList<TimeRange>();
        int start = 0;
        int end;
        for(Event occasion: events) {
            for(String member: request.getAttendees()) {
                // Check possibilities before occupied event.
                if(occasion.getAttendees().contains(member)) {
                    end = occasion.getWhen().start();
                    if(end - start >= request.getDuration()) {
                        workingTimes.add(TimeRange.fromStartDuration(start, end - start));
                    }
                    // Change start if endpoint of another person is greater
                    if(occasion.getWhen().end() > start) {
                        start = occasion.getWhen().end();
                    }
                }
            }
        }
        // Check possibilities using the end of the day
        end = END_OF_DAY;
        if(end - start >= request.getDuration()) {
            workingTimes.add(TimeRange.fromStartDuration(start, end - start));
        }
        return workingTimes;
    }
}
