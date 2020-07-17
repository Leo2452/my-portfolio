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

    private boolean containsMandatoryAttendees = false;
    private long MEETING_DURATION;

    /** Create a working schedule of meetings with TimeRanges. Prioritize
     *  mandatory attendees over optional guests, who can attend if at
     *  least one TimeRange works for mandatory attendees.
     */
    public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
        Collection<TimeRange> schedule = new ArrayList<TimeRange>();
        int start = TimeRange.START_OF_DAY;
        MEETING_DURATION = request.getDuration();

        for(Event occasion: events) {
            int end;
            boolean foundGuest = false;
            for(String mandatoryGuest: request.getAttendees()) {
                // Check possibilities before occupied event.
                if(occasion.getAttendees().contains(mandatoryGuest)) {
                    foundGuest = true;
                    break;
                }
            }
            if(foundGuest) {
                end = occasion.getWhen().start();
                if(meetingFits(start, end)) {
                    schedule.add(TimeRange.fromStartEnd(start, end, false));
                    containsMandatoryAttendees = true;
                }
                // Change start if endpoint current person's occasion is greater
                if(occasion.getWhen().end() > start) {
                    start = occasion.getWhen().end();
                }
            }
        }
        schedule = updateWithEndOfDay(schedule, start);
        schedule = updateWithOptionalAttendees(schedule, events, request);
        return schedule;
    }

    /** Searches through the optional attendees and attempts to place them in time blocks
     *  that work for mandatory attendees. Modifies the Collection of TimeRanges if so.
    */
    private Collection<TimeRange> updateWithOptionalAttendees(Collection<TimeRange> schedule,
                                                                Collection<Event> events, 
                                                                MeetingRequest request) {
        for(Event occasion: events) {
            boolean foundGuest = false;
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
                for(TimeRange currentTimeRange: schedule) {
                    if(currentTimeRange.contains(guestMeeting)){
                        schedule = splitTimeRange(schedule, currentTimeRange,
                                                    start, end);
                        break;
                    }
                }
            }
        }
        return schedule;
    }

    /** Handles the cases where a new time block may be open before or after the meeting. 
     *  Determines if the current TimeRange should be removed and updates schedule.
     */
    private Collection<TimeRange> splitTimeRange(Collection<TimeRange> schedule,
                                                TimeRange candidate, int start, 
                                                int end) {
        int newStartTime = candidate.start();
        int newEndTime = candidate.end();
        boolean canRemove = false;

        // Check block of time before meeting
        if(meetingFits(newStartTime, start)) {
            schedule.add(TimeRange.fromStartEnd(newStartTime, start, false));
            canRemove = true;
        }
        
        // Check block of time after meeting
        if(meetingFits(end, newEndTime)) {
            schedule.add(TimeRange.fromStartEnd(end, newEndTime, false));
            canRemove = true;
        }

        // Check block of time during meeting
        if(isSqueezedIn(start, newStartTime, end, newEndTime) 
            || !meetingFits(newStartTime, start)) {
            canRemove = true;
        }
        if(canRemove && isWorkable(schedule)) {
            schedule.remove(candidate);
        }
        return schedule;
    }

    /** Determines if the schedule works for everyone under guidelines:
     *  - Mandatory attendees require at least one TimeRange
     *  - All optional guests have to be accounted for, meaning they
     *    all shift the schedule and must keep 1+ TimeRange
     */
    private boolean isWorkable(Collection<TimeRange> schedule) {
        return schedule.size() > 1 || !containsMandatoryAttendees;
    }

    /** Determines if it completely covers the TimeRange block */
    private boolean isSqueezedIn(int start, int newStartTime, int end, int newEndTime) {
        return (newStartTime - start == 0 && newEndTime - end == 0);
    }

    private boolean meetingFits(int start, int end) {
        return meetingFits(MEETING_DURATION, start, end);
    }

    /** Determines if the timeframe for a meeting fits the duration */
    private boolean meetingFits(long duration, int start, int end) {
        return (end - start) >= ((int) duration);
    }

    /** Tries to add a meeting using the end of the variable from TimeRange.java */
    private Collection<TimeRange> updateWithEndOfDay(Collection<TimeRange> schedule,
                                                    int start) {
        int end = TimeRange.END_OF_DAY;
        if (meetingFits(start, end)) {
            schedule.add(TimeRange.fromStartEnd(start, end, true));
        }
        return schedule;
    }
}
