/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.texture

import com.almasb.fxgl.app.State
import com.almasb.fxgl.app.listener.StateListener
import javafx.geometry.Rectangle2D

/**
 * Represents an animated texture.
 * Animation channels, like WALK, RUN, IDLE, ATTACK, etc. can
 * be set dynamically to alter the animation.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AnimatedTexture(defaultChannel: AnimationChannel) : Texture(defaultChannel.image), StateListener {

    private var currentFrame = 0
    private var counter = 0.0

    private var playingChannel = false

    var animationChannel: AnimationChannel? = null
        set(value) {
            if (field !== value && !playingChannel) {
                reset()
                field = value
            }
        }

    init {
        animationChannel = defaultChannel

        // force channel to apply settings to this texture
        onUpdate(0.0)
    }

    fun playAnimationChannel(channel: AnimationChannel) {
        animationChannel = channel
        playingChannel = true
    }

    private lateinit var state: State

    var started = false
        private set

    fun start(state: State) {
        if (started) {
            return
        }

        this.state = state
        state.addStateListener(this)
        started = true
    }

    fun stop() {
        if (!started) {
            return
        }

        state.removeStateListener(this)
        reset()
        started = false
    }

    override fun onUpdate(tpf: Double) {
        animationChannel?.let {

            if (counter >= it.frameDuration) {

                // frame done

                if (currentFrame == it.sequence.size-1) {
                    // channel done
                    if (playingChannel) {
                        playingChannel = false
                    }
                }

                currentFrame = (currentFrame + 1) % it.sequence.size
                counter = 0.0
            }

            counter += tpf

            val framesPerRow = it.framesPerRow

            val frameWidth = it.frameWidth.toDouble()
            val frameHeight = it.frameHeight.toDouble()

            val row = it.sequence[currentFrame] / framesPerRow
            val col = it.sequence[currentFrame] % framesPerRow

            image = it.image
            fitWidth = frameWidth
            fitHeight = frameHeight
            viewport = Rectangle2D(col * frameWidth, row * frameHeight,
                    frameWidth, frameHeight)
        }
    }

    private fun reset() {
        currentFrame = 0
        counter = 0.0
    }
}