/*
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.ocs.dynamo.showcase.other;

import com.ocs.dynamo.showcase.Views;
import com.ocs.dynamo.ui.component.DefaultVerticalLayout;
import com.ocs.dynamo.ui.composite.form.UploadForm;
import com.ocs.dynamo.ui.composite.type.ScreenMode;
import com.ocs.dynamo.ui.view.BaseView;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.StreamResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.VerticalLayout;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * This view demonstrates the use of the upload form.
 */
@SpringView(name = Views.UPLOAD_FORM_VIEW)
@UIScope
@SuppressWarnings("serial")
public class UploadFormView extends BaseView {

    private class MyUploadForm extends UploadForm {

        public MyUploadForm(ScreenMode screenMode, boolean showCancelButton) {
            super(ProgressMode.PROGRESSBAR, screenMode, showCancelButton);
        }

        @Override
        protected int estimateSize(byte[] bytes) {
            return bytes.length;
        }

        @Override
        protected String getTitle() {
            return "My upload form";
        }

        @Override
        protected void process(final byte[] bytes, int estimatedSize) {

            if (bytes != null) {
                UploadFormView.this.image.setSource(new StreamResource(
                        new StreamResource.StreamSource() {
                            public InputStream getStream() {
                                return new ByteArrayInputStream(bytes);
                            }
                        }, "") {
                    @Override
                    public String getMIMEType() {
                        return "image/png";
                    }
                });
                UploadFormView.this.image.setVisible(true);
            }
        }

        @Override
        protected void cancel() {
            super.cancel();
            UploadFormView.this.image.setVisible(false);
        }
    }

    /** Vaadin vertical layout. */
    private VerticalLayout mainLayout;

    /** Dynamo pre-formatted upload form. */
    private MyUploadForm showCaseForm;

    /** Vaadin embedded content. */
    private Embedded image;

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    public void enter(ViewChangeEvent event) {

        // Apply Vaadin Layout.
        mainLayout = new DefaultVerticalLayout(true, true);

        // Upload images.
        showCaseForm = new MyUploadForm(ScreenMode.HORIZONTAL, true);
        showCaseForm.setAcceptFilter("image/*");
        showCaseForm.build();
        mainLayout.addComponent(showCaseForm);

        // Display uploaded image.
        image = new Embedded("Uploaded Image");
        mainLayout.addComponent(image);

        // Some plumbing.
        setCompositionRoot(mainLayout);
    }
}
