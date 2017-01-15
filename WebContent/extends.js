Number.prototype.toPercent = function(n) {
	n = n || 0;
	return (Math.round(this * Math.pow(10, n + 2)) / Math.pow(10, n)).toFixed(n) + '%';
}

$.extend({
	getUrlVars : function() {
		var vars = [], hash;
		var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
		for (var i = 0; i < hashes.length; i++) {
			hash = hashes[i].split('=');
			vars.push(hash[0]);
			vars[hash[0]] = hash[1].trim('#');
		}
		return vars;
	},
	getUrlVar : function(name) {
		return $.getUrlVars()[name];
	}
});
