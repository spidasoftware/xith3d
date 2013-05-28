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
package org.xith3d.ui.hud.dialogs;

import java.util.ArrayList;

import org.xith3d.scenegraph.Texture2D;
import org.xith3d.ui.hud.HUD;
import org.xith3d.ui.hud.base.AbstractButton;
import org.xith3d.ui.hud.base.TextWidget;
import org.xith3d.ui.hud.base.WidgetContainer;
import org.xith3d.ui.hud.base.Window;
import org.xith3d.ui.hud.base.WindowHeaderWidget;
import org.xith3d.ui.hud.base.__HUD_base_PrivilegedAccess;
import org.xith3d.ui.hud.borders.EmptyBorder;
import org.xith3d.ui.hud.layout.HullLayout;
import org.xith3d.ui.hud.layout.ListLayout;
import org.xith3d.ui.hud.listeners.ButtonListener;
import org.xith3d.ui.hud.listeners.DialogListener;
import org.xith3d.ui.hud.listeners.WindowAdapter;
import org.xith3d.ui.hud.widgets.Button;
import org.xith3d.ui.hud.widgets.Dialog;
import org.xith3d.ui.hud.widgets.Label;
import org.xith3d.ui.text2d.TextAlignment;

/**
 * This is a simple message displaying Dialog.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class MsgBox extends Dialog
{
    public static enum IconType
    {
        STANDARD,
        QUESTION,
        EXCEPTION,
        ERROR;
    }
    
    public static interface Factory
    {
        public Texture2D getIconTexture( IconType iconType );
        
        public void create( MsgBox msgbox );
    }
    
    private static class DefaultFactory implements Factory
    {
        public Texture2D getIconTexture( IconType iconType )
        {
            return ( null );
        }
        
        protected String getOKText()
        {
            return ( "OK" );
        }
        
        public void create( MsgBox msgbox )
        {
            final WidgetContainer pane = msgbox.getContentPane();
            
            Label msgLabel = new Label( -1f, -1f, "", TextAlignment.TOP_LEFT );
            
            pane.addWidget( msgLabel );
            msgbox.setMessageWidget( msgLabel );
            
            //pane.addWidget( new EmptyWidget( msgbox.getResX(), 10f ) );
            
            Button btnOK = new Button( 100f, 24f, getOKText() );
            btnOK.setUserObject( "OK" );
            
            pane.addWidget( btnOK );
            msgbox.setOKButton( btnOK );
        }
    }
    
    private static Factory factory = new DefaultFactory();
    
    private static final ArrayList< MsgBox > cache = new ArrayList< MsgBox >();
    
    private static class CacheManager extends WindowAdapter
    {
        @Override
        public void onWindowClosed( Window window )
        {
            synchronized ( cache )
            {
                cache.add( (MsgBox)window );
            }
        }
    }
    
    private CacheManager cacheManager = new CacheManager();
    
    public static void setFactory( Factory factory )
    {
        if ( factory == null )
            throw new IllegalArgumentException( "factory must not be null." );
        
        MsgBox.factory = factory;
    }
    
    public static final Factory getFactory()
    {
        return ( factory );
    }
    
    private TextWidget messageWidget = null;
    private Button btnOK = null;
    
    public void setMessageWidget( TextWidget widget )
    {
        this.messageWidget = widget;
    }
    
    public final TextWidget getMessageWidget()
    {
        return ( messageWidget );
    }
    
    public void setOKButton( Button button )
    {
        this.btnOK = button;
    }
    
    public final Button getOKButton()
    {
        return ( btnOK );
    }
    
    /**
     * Sets this MsgBox'es icon by type.
     * 
     * @param iconType
     */
    private void setIcon( IconType iconType )
    {
        
    }
    
    private void setMessage( String text )
    {
        getMessageWidget().setText( text );
    }
    
    private MsgBox()
    {
        super( 300f, 150f, "" );
        
        getContentPane().setClippingEnabled( false );
        
        this.getContentPane().setBorder( new EmptyBorder( 15, 15, 10, 15 ) );
        
        ListLayout listLayout = new ListLayout( ListLayout.Orientation.VERTICAL, 15f );
        listLayout.setOtherSpanCalculated( false );
        HullLayout hullLayout = new HullLayout( listLayout );
        
        getContentPane().setLayout( hullLayout );
        
        this.addWindowListener( cacheManager );
        
        factory.create( this );
        
        if ( this.getMessageWidget() == null )
        {
            throw new Error( "The currently used Factory doesn't set the message-widget." );
        }
        
        if ( this.getOKButton() == null )
        {
            throw new Error( "The currently used Factory doesn't set the OK-Button." );
        }
        
        this.setCloseCommand( this.getOKButton().getUserObject() );
        this.getOKButton().addButtonListener( new ButtonListener()
        {
            public void onButtonClicked( AbstractButton button, Object userObject )
            {
                MsgBox.this.detach();
            }
        } );
    }
    
    public static final MsgBox show( String title, String text, IconType iconType, HUD hud, DialogListener listener )
    {
        MsgBox msgbox;
        
        synchronized ( cache )
        {
            if ( cache.size() > 0 )
                msgbox = cache.remove( cache.size() - 1 );
            else
                msgbox = new MsgBox();
        }
        
        msgbox.removeAllDialogListeners();
        
        if ( listener != null )
        {
            msgbox.addDialogListener( listener );
        }
        
        WindowHeaderWidget whw = msgbox.getHeaderWidget();
        if ( whw != null )
            msgbox.getHeaderWidget().setText( title );
        msgbox.setIcon( iconType );
        msgbox.setMessage( text );
        
        __HUD_base_PrivilegedAccess.setTextureDirty( msgbox );
        
        hud.addWindowCentered( msgbox );
        
        return ( msgbox );
    }
    
    public static final MsgBox show( String text, IconType iconType, HUD hud, DialogListener listener )
    {
        return ( show( "Message", text, iconType, hud, listener ) );
    }
    
    public static final MsgBox show( String text, HUD hud, DialogListener listener )
    {
        return ( show( text, null, hud, listener ) );
    }
    
    public static final MsgBox show( String title, String text, IconType iconType, HUD hud )
    {
        return ( show( title, text, iconType, hud, null ) );
    }
    
    public static final MsgBox show( String text, IconType iconType, HUD hud )
    {
        return ( show( "Message", text, iconType, hud ) );
    }
    
    public static final MsgBox show( String text, HUD hud )
    {
        return ( show( text, null, hud ) );
    }
}
