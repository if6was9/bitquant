package bq.provider;

import java.time.Instant;
import java.time.LocalDate;
import java.util.stream.Stream;

import com.google.common.base.Optional;

import bq.OHLCV;
import bx.util.Zones;

public abstract class DataProvider {

	
	public class Request {
		LocalDate from;
		LocalDate to;		
		String symbol;
		
		
		public Optional<LocalDate> getFrom() {
			return Optional.of(from);
		}
		
		public Optional<LocalDate> getTo() {
			return Optional.of(to);
		}
		
		public Request from(LocalDate date) {
			this.from = date;
			return this;
		}
		
		
		public Request to(LocalDate date) {
		
			this.to = date;
			return this;
		}
	
		
		public Request symbol(String symbol) {
			this.symbol = symbol;
			return this;
		}
		
		public Stream<OHLCV> stream() {
			return fetch(this);
		}
	}
	
	public Request newRequest() {
		Request r = new Request();
		
		return r;
	}
	protected abstract Stream<OHLCV> fetch(Request request);
}
