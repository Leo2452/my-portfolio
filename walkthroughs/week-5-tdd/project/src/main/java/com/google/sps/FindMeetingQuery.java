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
                if(meetingFits(request.getDuration(), start, end)) {
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
        workingTimes = updateWithOptionalAttendees(workingTimes, events, request);
        return workingTimes;
    }

    /** Searches through the optional attendees and attempts to place them in time blocks
     *  that work for mandatory attendees. Modifies the Collection of TimeRanges if so.
    */
    private Collection<TimeRange> updateWithOptionalAttendees(Collection<TimeRange> workingTimes, 
                                                                Collection<Event> events, 
                                                                MeetingRequest request) {
        boolean foundGuest = false;
        for(Event occasion: events) {
            for(String guest: request.getOptionalAttendees()) {
                if(occasion.getAttendees().contains(guest)) {
                    foundGuest = true;
                    break;
                }
            }
            if(foundGuest) {
                int start = occasion.getWhen().start();
                int end = occasion.getWhen().end();
                boolean isAtEndOfDay = (end == TimeRange.END_OF_DAY)? true: false;
                TimeRange guestMeeting = TimeRange.fromStartEnd(start, end, isAtEndOfDay);
                for(TimeRange currentTimeRange: workingTimes) {
                    if(currentTimeRange.contains(guestMeeting)){
                        workingTimes = splitTimeRange(workingTimes, currentTimeRange, 
                                                        start, end, request.getDuration());
                        break;
                    }
                }
                foundGuest = true;
            }
        }
        return workingTimes;
    }

    /** Handles the cases where a new time block may be open before or after the meeting. 
     *  Determines if the current TimeRange should be removed and update workingTimes.
     */
    private Collection<TimeRange> splitTimeRange(Collection<TimeRange> workingTimes, 
                                                TimeRange candidate, int start, 
                                                int end, long duration) {
        int newStartTime = candidate.start();
        int newEndTime = candidate.end();
        boolean remove = false;

        // Check block of time before meeting
        if(newStartTime != start && meetingFits(duration, newStartTime, start)) {
            workingTimes.add(TimeRange.fromStartEnd(newStartTime, start, false));
            remove = true;
        }
        
        // Check block of time after meeting
        if(newEndTime != end && meetingFits(duration, end, newEndTime)) {
            workingTimes.add(TimeRange.fromStartEnd(end, newEndTime, false));
            remove = true;
        }

        // Check block of time during meeting
        if(squeezedIn(start, newStartTime, end, newEndTime)) {
            remove = true;
        }
        if(meetingFits(duration, start, end)) {
            remove = true;
        }
        if(remove) {
            workingTimes.remove(candidate);
        }
        return workingTimes;
    }

    /** Determines if it completely covers the TimeRange block */
    private boolean squeezedIn(int start, int newStartTime, int end, int newEndTime) {
        return (newStartTime - start == 0 && newEndTime - end == 0);
    }

    /** Determines if the timeframe for a meeting fits the duration */
    private boolean meetingFits(long duration, int start, int end) {
        return (end - start) >= ((int) duration);
    }

    /** Tries to add a meeting using the end of the variable from TimeRange.java */
    private Collection<TimeRange> updateWithEndOfDay(Collection<TimeRange> workingTimes, 
                                                    int start, long duration) {
        int end = TimeRange.END_OF_DAY;
        if (meetingFits(duration, start, end)) {
            workingTimes.add(TimeRange.fromStartEnd(start, end, true));
        }
        return workingTimes;
    }
}
