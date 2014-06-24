// for uPortal 4.0.x on mobile
// - it uses jquery-mobile 1.1.1
// - with up-mobile-config.js which has ajaxEnabled=false
// - but hashListeningEnabled/linkBindingEnabled/pushStateEnabled are enabled (which is the default)
// - this causes havoc with angularjs which does the same kind of handling (hashchange handling)
// (cf http://stackoverflow.com/questions/10904433/jquery-mobile-require-js-and-backbone)
if (window.up && window.up.jQuery && window.up.jQuery.mobile) {
    window.up.jQuery.mobile.ajaxEnabled = false;
    window.up.jQuery.mobile.hashListeningEnabled = false;
    window.up.jQuery.mobile.linkBindingEnabled = false;
    window.up.jQuery.mobile.pushStateEnabled = false;
}
