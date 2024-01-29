'use strict';
use(function () {

  var data = {};

  data.icons = [
  <%
    // Sort Icons alphabetically
    var glyphsSorted = _.sortBy(glyphs, function(obj) { return obj.name });

  _.each(glyphsSorted, function(glyph) {
    %>"<%= className %>-<%= glyph.name %>",
  <% }); %>
  ];

  return data;

});
