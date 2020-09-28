package org.recap.model.search;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajeshbabuk on 12/10/16.
 */
public class CollectionForm extends BibliographicMarcForm {

    private String itemBarcodes;
    private boolean showResults = false;
    private boolean selectAll = false;
    private String errorMessage;
    private String barcodesNotFoundErrorMessage;
    private String ignoredBarcodesErrorMessage;
    private List<SearchResultRow> searchResultRows = new ArrayList<>();
    private boolean showModal = false;

    /**
     * Gets item barcodes.
     *
     * @return the item barcodes
     */
    public String getItemBarcodes() {
        return itemBarcodes;
    }

    /**
     * Sets item barcodes.
     *
     * @param itemBarcodes the item barcodes
     */
    public void setItemBarcodes(String itemBarcodes) {
        this.itemBarcodes = itemBarcodes;
    }

    /**
     * Is show results boolean.
     *
     * @return the boolean
     */
    public boolean isShowResults() {
        return showResults;
    }

    /**
     * Sets show results.
     *
     * @param showResults the show results
     */
    public void setShowResults(boolean showResults) {
        this.showResults = showResults;
    }

    /**
     * Is select all boolean.
     *
     * @return the boolean
     */
    public boolean isSelectAll() {
        return selectAll;
    }

    /**
     * Sets select all.
     *
     * @param selectAll the select all
     */
    public void setSelectAll(boolean selectAll) {
        this.selectAll = selectAll;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Gets barcodes not found error message.
     *
     * @return the barcodes not found error message
     */
    public String getBarcodesNotFoundErrorMessage() {
        return barcodesNotFoundErrorMessage;
    }

    /**
     * Sets barcodes not found error message.
     *
     * @param barcodesNotFoundErrorMessage the barcodes not found error message
     */
    public void setBarcodesNotFoundErrorMessage(String barcodesNotFoundErrorMessage) {
        this.barcodesNotFoundErrorMessage = barcodesNotFoundErrorMessage;
    }

    /**
     * Gets ignored barcodes error message.
     *
     * @return the ignored barcodes error message
     */
    public String getIgnoredBarcodesErrorMessage() {
        return ignoredBarcodesErrorMessage;
    }

    /**
     * Sets ignored barcodes error message.
     *
     * @param ignoredBarcodesErrorMessage the ignored barcodes error message
     */
    public void setIgnoredBarcodesErrorMessage(String ignoredBarcodesErrorMessage) {
        this.ignoredBarcodesErrorMessage = ignoredBarcodesErrorMessage;
    }

    /**
     * Gets search result rows.
     *
     * @return the search result rows
     */
    public List<SearchResultRow> getSearchResultRows() {
        if (null == searchResultRows) {
            searchResultRows = new ArrayList<>();
        }
        return searchResultRows;
    }

    /**
     * Sets search result rows.
     *
     * @param searchResultRows the search result rows
     */
    public void setSearchResultRows(List<SearchResultRow> searchResultRows) {
        this.searchResultRows = searchResultRows;
    }

    /**
     * Is show modal boolean.
     *
     * @return the boolean
     */
    public boolean isShowModal() {
        return showModal;
    }

    /**
     * Sets show modal.
     *
     * @param showModal the show modal
     */
    public void setShowModal(boolean showModal) {
        this.showModal = showModal;
    }
}
