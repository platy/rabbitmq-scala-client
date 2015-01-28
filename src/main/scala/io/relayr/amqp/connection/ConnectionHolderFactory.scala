package io.relayr.amqp.connection

import com.rabbitmq.client.ConnectionFactory
import io.relayr.amqp.ReconnectionStrategy.{ NoReconnect, JavaClientFixedReconnectDelay }
import io.relayr.amqp.{ ConnectionHolder, EventHooks, ReconnectionStrategy }

import scala.concurrent.ExecutionContext

private[amqp] class ConnectionHolderFactory(connectionFactory: ConnectionFactory, reconnectionStrategy: ReconnectionStrategy, eventHooks: EventHooks, executionContext: ExecutionContext) {
  def newConnectionHolder(): ConnectionHolder = {
    reconnectionStrategy match {
      case NoReconnect ⇒ ()
      case JavaClientFixedReconnectDelay(networkRecoveryInterval) ⇒
        connectionFactory.setAutomaticRecoveryEnabled(true)
        connectionFactory.setNetworkRecoveryInterval(networkRecoveryInterval.toMillis)
    }
    new ReconnectingConnectionHolder(connectionFactory, eventHooks.event, ChannelOwnerImpl)
  }
}
