/**
 * Copyright (c) 2003-2009, Xith3D Project Group all rights reserved.
 * 
 * Portions based on the Java3D interface, Copyright by Sun Microsystems.
 * Many thanks to the developers of Java3D and Sun Microsystems for their
 * innovation and design.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * Neither the name of the 'Xith3D Project Group' nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) A
 * RISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE
 */
package org.xith3d.input;

import org.jagatoo.input.InputSystem;
import org.jagatoo.input.InputSystemException;
import org.jagatoo.input.devices.components.DeviceComponent;
import org.jagatoo.input.devices.components.MouseButtons;
import org.jagatoo.input.events.MouseMovedEvent;
import org.jagatoo.input.events.MouseWheelEvent;
import org.jagatoo.input.handlers.InputHandler;
import org.jagatoo.input.listeners.MouseAdapter;
import org.jagatoo.input.managers.InputBindingsManager;
import org.openmali.FastMath;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.input.modules.orih.ORIHInputAction;
import org.xith3d.input.modules.orih.ORIHInputBindingsManager;
import org.xith3d.input.modules.orih.ORIHInputStatesManager;
import org.xith3d.scenegraph.Transformable;
import org.xith3d.scenegraph.View;

/**
 * Rotates a Transformable around its center.
 * 
 * @author Jens  Lehmann
 * @author Abdul Bezrati
 * @author William Denniss
 * @author Marvin Froehlich (aka Qudus)
 */
public class ObjectRotationInputHandler extends InputHandler< ORIHInputAction >
{
    private Transformable transTrg;
    private View view;
    
    private float rotX   = 0f,
                  rotY   = 0f;
    
    private float mouseXSpeed = 1.0f,
                  mouseYSpeed = -1.0f;
    
    private float discreteZoomStep = 0.5f;
    private int discreteZoomDelta = 0;
    
    private Vector3f tmpVec1 = new Vector3f();
    private Vector3f tmpVec2 = new Vector3f();
    
    private boolean      isRotationScheduled = false;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final ORIHInputBindingsManager getBindingsManager()
    {
        return ( (ORIHInputBindingsManager)super.getBindingsManager() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final ORIHInputStatesManager getStatesManager()
    {
        return ( (ORIHInputStatesManager)super.getStatesManager() );
    }
    
    /**
     * Sets the mouse movement speed for the x-axis.
     * 
     * @param speedX the new speed for the x-axis
     */
    public void setMouseSpeedX( float speedX )
    {
        this.mouseXSpeed = speedX;
    }
    
    /**
     * @return the mouse movement speed for the x-axis
     */
    public final float getMouseSpeedX()
    {
        return ( mouseXSpeed );
    }
    
    /**
     * Sets the mouse movement speed for the y-axis.
     * 
     * @param speedY the new speed for the y-axis
     */
    public void setMouseSpeedY( float speedY )
    {
        this.mouseYSpeed = speedY;
    }
    
    /**
     * @return the mouse movement speed for the y-axis
     */
    public final float getMouseSpeedY()
    {
        return ( mouseYSpeed );
    }
    
    /**
     * Sets the stepsize of discrete zooming.
     * 
     * @param stepSize
     */
    public void setDiscreteZoomStep( float stepSize )
    {
        this.discreteZoomStep = stepSize;
    }
    
    /**
     * @return the stepsize of discrete zooming.
     */
    public final float getDiscreteZoomStep()
    {
        return ( discreteZoomStep );
    }
    
    public void setTransformTarget( Transformable trans )
    {
        this.transTrg = trans;
        
        updateFromTransformable();
    }
    
    public Transformable getTransformTarget()
    {
        return ( transTrg );
    }
    
    public void setView( View view )
    {
        this.view = view;
    }
    
    public final View getView()
    {
        return ( view );
    }
    
    public void updateFromTransformable()
    {
        rotX = FastMath.asin( getTransformTarget().getTransform().getMatrix4f().m02() );
        rotY = FastMath.asin( getTransformTarget().getTransform().getMatrix4f().m21() );
    }
    
    public void updateTransformable()
    {
        getTransformTarget().getTransform().getTranslation( tmpVec1 );
        getTransformTarget().getTransform().rotXYZ( rotY, rotX, 0f );
        getTransformTarget().getTransform().setTranslation( tmpVec1 );
        getTransformTarget().setTransform( getTransformTarget().getTransform() );
    }
    
    private final void limitEuler( Tuple3f euler )
    {
        euler.setX( euler.getX() % FastMath.TWO_PI );
        euler.setY( euler.getY() % FastMath.TWO_PI );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void update( long nanoSeconds, float seconds, long nanoFrame, float frameSeconds ) throws InputSystemException
    {
        if ( isKeyboardSuspended() && isMouseMovementSuspended() )
            return;
        
        if ( isRotationScheduled && !isMouseMovementSuspended() )
        {
            updateTransformable();
            
            isRotationScheduled = false;
        }
        
        if ( !isKeyboardSuspended() )
        {
            final ORIHInputStatesManager statesManager = getStatesManager();
            
            if ( statesManager.isRotating() )
            {
                transTrg.getTransform().getEuler( tmpVec1 );
                
                if ( statesManager.isRotatingLeft() )
                    tmpVec1.addY( getMouseSpeedX() / 25f );
                if ( statesManager.isRotatingRight() )
                    tmpVec1.subY( getMouseSpeedY() / 25f );
                if ( statesManager.isRotatingUp() )
                    tmpVec1.addX( mouseYSpeed / 25f );
                if ( statesManager.isRotatingDown() )
                    tmpVec1.subX( mouseYSpeed / 25f );
                
                limitEuler( tmpVec1 );
                
                transTrg.getTransform().setEuler( tmpVec1 );
                transTrg.setTransform( transTrg.getTransform() );
            }
        }
        
        if ( discreteZoomDelta != 0 )
        {
            float zoomDist = discreteZoomDelta * discreteZoomStep;
            discreteZoomDelta = 0;
            
            if ( view != null )
            {
                transTrg.getPosition( tmpVec1 );
                view.getPosition( tmpVec2 );
                
                tmpVec2.sub( tmpVec1 );
                final float len = tmpVec2.length();
                tmpVec2.normalize();
                zoomDist += len;
                zoomDist = Math.max( zoomDist, discreteZoomStep );
                tmpVec2.scale( zoomDist );
                
                tmpVec2.add( tmpVec1 );
                
                view.setPosition( tmpVec2 );
            }
        }
    }
    
    private final void checkDiscreteZoom( DeviceComponent comp )
    {
        final ORIHInputAction action = getBindingsManager().getBoundAction( comp );
        
        if ( action != null )
        {
            switch ( action )
            {
                case DISCRETE_ZOOM_IN:
                    discreteZoomDelta--;
                    break;
                
                case DISCRETE_ZOOM_OUT:
                    discreteZoomDelta++;
                    break;
            }
        }
    }
    
    private class MouseLstnr extends MouseAdapter
    {
        @Override
        public void onMouseMoved( MouseMovedEvent e, int x, int y, int dx, int dy )
        {
            if ( isMouseMovementSuspended() )
                return;
            
            if ( e.getMouse().getButtonsState() != 0 )
            {
                rotX             += ( dx / 150f ) * getMouseSpeedX();
                rotY             += ( dy / 150f ) * getMouseSpeedY();
                
                isRotationScheduled = true;
            }
        }
        
        @Override
        public void onMouseWheelMoved( MouseWheelEvent e, int wheelDelta )
        {
            if ( isMouseWheelSuspended() )
                return;
            
            if ( e.getWheelDelta() > 0 )
                checkDiscreteZoom( MouseButtons.WHEEL_UP );
            else if ( e.getWheelDelta() < 0 )
                checkDiscreteZoom( MouseButtons.WHEEL_DOWN );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setSuspendMask( int suspendMask )
    {
        final boolean wasSuspended = isSuspended();
        
        if ( super.setSuspendMask( suspendMask ) )
        {
            if ( wasSuspended && !isSuspended() )
            {
                updateFromTransformable();
            }
            else if ( !wasSuspended && isSuspended() )
            {
                rotX = FastMath.asin( getTransformTarget().getTransform().getMatrix4f().m02() );
                rotY = FastMath.asin( getTransformTarget().getTransform().getMatrix4f().m21() );
            }
            
            return ( true );
        }
        
        return ( false );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setInputSystem( InputSystem inputSystem )
    {
        super.setInputSystem( inputSystem );
        
        inputSystem.getMouse().addMouseListener( new MouseLstnr() );
        //inputManager.setMouseAbsolute( false );
    }
    
    @Override
    protected ORIHInputStatesManager createInputStatesManager( InputBindingsManager< ORIHInputAction > bindingsManager )
    {
        return ( new ORIHInputStatesManager( this ) );
    }
    
    public ObjectRotationInputHandler( Transformable transNode, float mouseXSpeed, float mouseYSpeed )
    {
        super( new ORIHInputBindingsManager() );
        
        this.transTrg = transNode;
        
        this.mouseXSpeed = mouseXSpeed;
        this.mouseYSpeed = mouseYSpeed;
        
        rotX = FastMath.asin( transNode.getTransform().getMatrix4f().m02() );
        rotY = FastMath.asin( transNode.getTransform().getMatrix4f().m21() );
    }
    
    public ObjectRotationInputHandler( Transformable transNode )
    {
        this( transNode, 1.0f, -1.0f );
    }
    
    public ObjectRotationInputHandler( Transformable transNode, float mouseXSpeed, float mouseYSpeed, View view )
    {
        this( transNode, mouseXSpeed, mouseYSpeed );
        
        this.view = view;
    }
    
    public ObjectRotationInputHandler( Transformable transNode, View view )
    {
        this( transNode, 1.0f, -1.0f, view );
    }
    
    public static ObjectRotationInputHandler createDefault( Transformable transNode, float mouseXSpeed, float mouseYSpeed )
    {
        final ObjectRotationInputHandler morih = new ObjectRotationInputHandler( transNode, mouseXSpeed, mouseYSpeed );
        
        morih.getBindingsManager().createDefaultBindings();
        
        return ( morih );
    }
    
    public static ObjectRotationInputHandler createDefault( Transformable transNode )
    {
        return ( createDefault( transNode, 1.0f, -1.0f ) );
    }
    
    public static ObjectRotationInputHandler createDefault( Transformable transNode, float mouseXSpeed, float mouseYSpeed, View view )
    {
        final ObjectRotationInputHandler morih = createDefault( transNode, mouseXSpeed, mouseYSpeed );
        
        morih.setView( view );
        
        return ( morih );
    }
    
    public static ObjectRotationInputHandler createDefault( Transformable transNode, View view )
    {
        return ( createDefault( transNode, 1.0f, -1.0f, view ) );
    }
}
