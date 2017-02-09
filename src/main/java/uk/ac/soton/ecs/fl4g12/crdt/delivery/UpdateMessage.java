/*
 * The MIT License
 *
 * Copyright 2017 Fabrizio Lungo <fl4g12@ecs.soton.ac.uk>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package uk.ac.soton.ecs.fl4g12.crdt.delivery;

import java.io.Serializable;

/**
 * A message containing an update. Messages are used to communicate the relevant changes that need to be applied to
 * other nodes.
 *
 * The contents of the message will be defined by subclasses and will typically be specific for the {@link Updatable}
 * which they are used for. Generics are used as part of the {@link Updatable} interface in order to allow this.
 *
 * Implementations of {@linkplain UpdateMessage} should be serializable so that the {@link DeliveryChannel} which
 * communicates the messages can serialize them.
 *
 * @param <K> the type of identifier used to identify nodes.
 */
public interface UpdateMessage<K> extends Serializable {

    /**
     * Gets the identifier of the node that generated the message.
     *
     * @return the identifier of the node which created the message.
     */
    K getIdentifier();

}
