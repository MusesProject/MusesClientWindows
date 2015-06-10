package eu.musesproject.windowsclient.connectionmanager;

import java.util.concurrent.TimeUnit;

/*
 * #%L
 * musesclient
 * %%
 * Copyright (C) 2013 - 2014 HITEC
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

public class RequestTimeoutTimer extends CountDownTimer {
	public interface RequestTimeoutHandler {
		void handleRequestTimeout(int requestId);
	}
	
	private static final int REQUEST_TIMEOUT_SECONDS = (int) TimeUnit.SECONDS.toMillis(5);
	
	private RequestTimeoutHandler requestTimeoutHandler;
	private int requestId;
	

	public RequestTimeoutTimer(RequestTimeoutHandler requestTimeoutHandler, int requestId) {
		super(REQUEST_TIMEOUT_SECONDS);
		this.requestTimeoutHandler = requestTimeoutHandler;
		this.requestId = requestId;
	}

	@Override
	public void onFinish() {
		if(requestTimeoutHandler != null) {
			requestTimeoutHandler.handleRequestTimeout(requestId);
		}
	}

	@Override
	public void cancel() {
		requestTimeoutHandler = null;
	}
}