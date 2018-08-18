/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bulkcheckout;

/**
 *
 * @author yogesh.gandhi
 */
@FunctionalInterface
public interface VersionControl {
    public String checkout(final String file, String comments);
}
