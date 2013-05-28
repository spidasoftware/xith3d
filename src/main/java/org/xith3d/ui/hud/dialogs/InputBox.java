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
import org.xith3d.ui.hud.widgets.Panel;
import org.xith3d.ui.hud.widgets.TextField;
import org.xith3d.ui.text2d.TextAlignment;

/**
 * This is a simple Dialog querying for a String to be inputed.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class InputBox extends Dialog
{
    public static interface InputBoxListener
    {
        public void onInputBoxClosed( InputBox inputBox, boolean isCancelled, String textValue );
    }
    
    public static interface Factory
    {
        public Texture2D getIconTexture();
        
        public void create( InputBox inputBox );
    }
    
    private static class DefaultFactory implements Factory
    {
        public Texture2D getIconTexture()
        {
            return ( null );
        }
        
        protected String getOKText()
        {
            return ( "OK" );
        }
        
        protected String getCancelText()
        {
            return ( "Cancel" );
        }
        
        public void create( InputBox inputBox )
        {
            final WidgetContainer pane = inputBox.getContentPane();
            
            Label msgLabel = new Label( -1f, -1f, "", TextAlignment.TOP_LEFT );
            
            pane.addWidget( msgLabel );
            inputBox.setMessageWidget( msgLabel );
            
            //pane.addWidget( new EmptyWidget( inputBox.getResX(), 10f ) );
            
            TextField textField = new TextField( 100f, 24f, "" );
            
            pane.addWidget( textField );
            inputBox.setTextField( textField );
            
            Panel buttonsPanel = new Panel( false, 205f, 24f );
            buttonsPanel.setClippingEnabled( false );
            ListLayout ll = new ListLayout( ListLayout.Orientation.HORIZONTAL, 5f );
            ll.setAlignment( ListLayout.Alignment.RIGHT_CENTER );
            buttonsPanel.setLayout( ll );
            
            Button btnOK = new Button( 100f, 24f, getOKText() );
            btnOK.setUserObject( "OK" );
            
            inputBox.setOKButton( btnOK );
            buttonsPanel.addWidget( btnOK );
            
            Button btnCancel = new Button( 100f, 24f, getCancelText() );
            btnCancel.setUserObject( "CANCEL" );
            
            inputBox.setCancelButton( btnCancel );
            buttonsPanel.addWidget( btnCancel );
            
            pane.addWidget( buttonsPanel );
        }
    }
    
    private static Factory factory = new DefaultFactory();
    
    private static final ArrayList< InputBox > cache = new ArrayList< InputBox >();
    
    private static class CacheManager extends WindowAdapter
    {
        @Override
        public void onWindowClosed( Window window )
        {
            synchronized ( cache )
            {
                cache.add( (InputBox)window );
            }
        }
    }
    
    private final CacheManager cacheManager = new CacheManager();
    
    public static void setFactory( Factory factory )
    {
        if ( factory == null )
            throw new IllegalArgumentException( "factory must not be null." );
        
        InputBox.factory = factory;
    }
    
    public static final Factory getFactory()
    {
        return ( factory );
    }
    
    private TextWidget messageWidget = null;
    private TextField textField = null;
    private Button btnOK = null;
    private Button btnCancel = null;
    
    private InputBoxListener listener = null;
    
    private boolean cancelled = false;
    
    public void setMessageWidget( TextWidget widget )
    {
        this.messageWidget = widget;
    }
    
    public final TextWidget getMessageWidget()
    {
        return ( messageWidget );
    }
    
    public void setTextField( TextField textField )
    {
        this.textField = textField;
    }
    
    public final TextField getTextField()
    {
        return ( textField );
    }
    
    public void setOKButton( Button button )
    {
        this.btnOK = button;
    }
    
    public final Button getOKButton()
    {
        return ( btnOK );
    }
    
    public void setCancelButton( Button button )
    {
        this.btnCancel = button;
    }
    
    public final Button getCancelButton()
    {
        return ( btnCancel );
    }
    
    public void setMessage( String message )
    {
        getMessageWidget().setText( message );
    }
    
    public void setText( String text )
    {
        getTextField().setText( text );
    }
    
    public final String getText()
    {
        return ( getTextField().getText() );
    }
    
    public final boolean isCancelled()
    {
        return ( cancelled );
    }
    
    private InputBox()
    {
        super( 300f, 150f, "" );
        
        getContentPane().setClippingEnabled( false );
        
        this.getContentPane().setBorder( new EmptyBorder( 15, 15, 10, 15 ) );
        
        ListLayout listLayout = new ListLayout( ListLayout.Orientation.VERTICAL, 15f );
        listLayout.setOtherSpanCalculated( true );
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
        
        if ( this.getCancelButton() == null )
        {
            throw new Error( "The currently used Factory doesn't set the Cancel-Button." );
        }
        
        this.getOKButton().addButtonListener( new ButtonListener()
        {
            public void onButtonClicked( AbstractButton button, Object userObject )
            {
                InputBox.this.setCloseCommand( button.getUserObject() );
                InputBox.this.cancelled = false;
                InputBox.this.detach();
                
                if ( InputBox.this.listener != null )
                    InputBox.this.listener.onInputBoxClosed( InputBox.this, false, InputBox.this.getText() );
            }
        } );
        
        this.getCancelButton().addButtonListener( new ButtonListener()
        {
            public void onButtonClicked( AbstractButton button, Object userObject )
            {
                InputBox.this.setCloseCommand( button.getUserObject() );
                InputBox.this.cancelled = true;
                InputBox.this.detach();
                
                if ( InputBox.this.listener != null )
                    InputBox.this.listener.onInputBoxClosed( InputBox.this, true, InputBox.this.getText() );
            }
        } );
    }
    
    private static final InputBox show( String title, String message, String initialValue, HUD hud, DialogListener dialogListener, InputBoxListener inputBoxListener )
    {
        InputBox inputBox;
        
        synchronized ( cache )
        {
            if ( cache.size() > 0 )
                inputBox = cache.remove( cache.size() - 1 );
            else
                inputBox = new InputBox();
        }
        
        inputBox.removeAllDialogListeners();
        
        if ( dialogListener != null )
        {
            inputBox.addDialogListener( dialogListener );
        }
        
        inputBox.listener = inputBoxListener;
        
        WindowHeaderWidget whw = inputBox.getHeaderWidget();
        if ( whw != null )
            inputBox.getHeaderWidget().setText( title );
        //inputBox.setIcon( iconType );
        inputBox.setMessage( message );
        inputBox.setText( initialValue );
        
        /*
        if ( initialValue.length() > 0 )
        {
            inputBox.getTextField().setSelectionStart( 0 );
            inputBox.getTextField().setSelectionEnd( initialValue.length() );
        }
        */
        
        __HUD_base_PrivilegedAccess.setTextureDirty( inputBox );
        
        hud.addWindowCentered( inputBox );
        
        inputBox.getTextField().requestFocus();
        
        return ( inputBox );
    }
    
    public static final InputBox show( String title, String message, String initialValue, HUD hud, DialogListener listener )
    {
        return ( show( title, message, initialValue, hud, listener, null ) );
    }
    
    public static final InputBox show( String message, String initialValue, HUD hud, DialogListener listener )
    {
        return ( show( "Text-Input", message, initialValue, hud, listener ) );
    }
    
    public static final InputBox show( String initialValue, HUD hud, DialogListener listener )
    {
        return ( show( "Text-Input:", initialValue, hud, listener ) );
    }
    
    public static final InputBox show( String title, String message, String initialValue, HUD hud, InputBoxListener listener )
    {
        return ( show( title, message, initialValue, hud, null, listener ) );
    }
    
    public static final InputBox show( String message, String initialValue, HUD hud, InputBoxListener listener )
    {
        return ( show( "Text-Input", message, initialValue, hud, listener ) );
    }
    
    public static final InputBox show( String initialValue, HUD hud, InputBoxListener listener )
    {
        return ( show( "Text-Input:", initialValue, hud, listener ) );
    }
    
    public static final InputBox show( String message, String initialValue, HUD hud )
    {
        return ( show( "Text-Input", message, initialValue, hud, null, null ) );
    }
    
    public static final InputBox show( String initialValue, HUD hud )
    {
        return ( show( "Text-Input:", initialValue, hud ) );
    }
}
