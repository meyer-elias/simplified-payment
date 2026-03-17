package com.eliasmeyer.sp.infrastructure.persistence.eventstore;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "TB_OUTBOX_EVENT", indexes = {
	@Index(name = "IX_STATUS", columnList = "STATUS")
})
class OutboxEventEntity {

	@Id
	private UUID id;

	@Column(name = "AGGREGATE_TYPE", nullable = false)
	private String aggregateType;

	@Column(name = "AGGREGATE_ID", nullable = false)
	private String aggregateId;

	@Column(name = "EVENT_TYPE", nullable = false)
	private String eventType;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "PAYLOAD", nullable = false, columnDefinition = "jsonb")
	private String payload; // JSON serializado do evento

	@Column(name = "OCCURRED_ON", nullable = false)
	private Instant occurredOn;

	@Column(name = "STATUS", nullable = false, columnDefinition = "SMALLINT")
	@Convert(converter = OutboxEventStatusConverter.class)
	private OutboxEventStatus status;

	@Column(name = "PUBLISHED_AT")
	private Instant publishedAt;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getAggregateType() {
		return aggregateType;
	}

	public void setAggregateType(String aggregateType) {
		this.aggregateType = aggregateType;
	}

	public String getAggregateId() {
		return aggregateId;
	}

	public void setAggregateId(String aggregateId) {
		this.aggregateId = aggregateId;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public Instant getOccurredOn() {
		return occurredOn;
	}

	public void setOccurredOn(Instant occurredOn) {
		this.occurredOn = occurredOn;
	}

	public OutboxEventStatus getStatus() {
		return status;
	}

	public void setStatus(OutboxEventStatus status) {
		this.status = status;
	}

	public Instant getPublishedAt() {
		return publishedAt;
	}

	public void setPublishedAt(Instant publishedAt) {
		this.publishedAt = publishedAt;
	}
}
