/**
 *  This will add a button the sidekick, when clicked will attempt a download of the ccurrent page in PDF form.
 *
 *  For example. if on '/content/geometrixx/en.html' - once clicked the downloaded path will be '/content/geometrixx/en.docrender.pdf'
 *
 *  A servlet accepting all 'foundation/components/page' root resource types will handle rendition of the resource into PDF
 */
(function() {
    var _startCondition = true;
    var init = function(sidekick) {
        var button, insertNumber;
        button = new CQ.Ext.Button({
            id: 'trp-ecl-sidekick-wcmmode-disabled',
            iconCls: 'trp-ecl-sidekick-wcmmode-disabled',
            tooltip: {
                title: 'Download PDF',
                text: 'Generates a PDF of the current page for immediate download.'
            },
            handler: function() {
                var pagePath = CQ.WCM.getPagePath();
                var fileName = pagePath.substring(pagePath.lastIndexOf("/") + 1);
                var anchor = document.createElement('a');
                anchor.href = pagePath + ".docrender.pdf";
                anchor.target = '_blank';
                anchor.download = fileName + ".pdf";
                document.body.appendChild(anchor);
                anchor.click();
                this.disable(); // so we won't click too many times
                document.body.removeChild(anchor);
                CQ.Notification.notify("PDF Generation","PDF is being generated, please stand by");
            }
        });
        insertNumber = sidekick.getBottomToolbar().items.length;
        return sidekick.getBottomToolbar().insert(insertNumber, button);
    };
    if (_startCondition) {
        return CQ.WCM.on('sidekickready', function(sidekick) {
            return sidekick.on('loadcontent', function() {
                return init(sidekick);
            });
        });
    }
})();
