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
package com.ocs.dynamo.showcase;

/**
 * Constant class for the show case Views.
 */
public final class Views {

    /**
     * Search screen for movies
     */
    public static final String MOVIES_VIEW = "MoviesView";

    /**
     * Horizontal split view for movies
     */
    public static final String HORIZONTAL_MOVIES_SPLIT_VIEW = "HorizontalMoviesSplitView";

    /**
     * Vertical split view for movies
     */
    public static final String VERTICAL_MOVIES_SPLIT_VIEW = "VerticalMoviesSplitView";

    /**
     * Editable table view
     */
    public static final String TABULAR_MOVIES_VIEW = "TabularMoviesView";

    /**
     * Upload form view.
     */
    public static final String UPLOAD_FORM_VIEW = "UploadFormView";

    /**
     * Only use static members.
     */
    private Views() {
    }
}
