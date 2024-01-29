const stylelint = require('stylelint');

const ruleName = 'rules/empty-line-between-blocks';

const messages = stylelint.utils.ruleMessages(ruleName, ({
  expected: (block) => `Expected empty line before "${ block }" selector`
}));

const hasEmptyLine = function(string) {
  if (string === '' || string === undefined) {
    return false;
  }

  return /\n[\r\t ]*\n/.test(string);
};


module.exports = stylelint.createPlugin(ruleName, function(expectation, options, context) {

  // Return if rule is disabled
  if (!expectation) { return; }

  return function(root, result) {
    const validOptions = stylelint.utils.validateOptions({
      ruleName: ruleName,
      result: result,
      actual: expectation
    });

    if (!validOptions) {
      return;
    }

    root.walkRules(check);
    root.walkAtRules(check);

    // Rule Expectation
    function check (statement) {

      (statement.nodes || []).forEach(node => {

        const nextNode = node.next();

        if (!nextNode) { return; }

        // Only Allow 'rule' Nodes
        if (nextNode.type !== 'rule') { return; }
        if (node.type === 'comment') { return; }

        const hasEmptyLineBefore = hasEmptyLine(nextNode.raws.before);

        // Return if the expectation is met
        if (hasEmptyLineBefore) { return; }

        stylelint.utils.report({
          ruleName,
          result,
          node: nextNode,
          message: messages.expected(nextNode.selector)
        });
      });
    }
  };
});
