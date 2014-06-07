package scalatags
package generic
import acyclic.file
import scalatags.Platform._
import scalatags._

/**
 * Created by haoyi on 6/2/14.
 */
trait Util[Target] {

  type ConcreteHtmlTag[T <: Platform.Base] <: TypedTag[T, Target]
  def makeAbstractTypedTag[T <: Base](tag: String, void: Boolean): ConcreteHtmlTag[T]
  protected[this] implicit def stringAttr: AttrValue[Target, String]
  protected[this] implicit def booleanAttr: AttrValue[Target, Boolean]
  protected[this] implicit def numericAttr[T: Numeric]: AttrValue[Target, T]
  protected[this] implicit def stringStyle: StyleValue[Target, String]
  protected[this] implicit def booleanStyle: StyleValue[Target, Boolean]
  protected[this] implicit def numericStyle[T: Numeric]: StyleValue[Target, T]

  /**
   * Provides extension methods on strings to fit them into Scalatag fragments.
   */
  implicit class ExtendedString(s: String){
    /**
     * Converts the string to a [[HtmlTag]]
     */
    def tag[T <: Base] = {
      if (!Escaping.validTag(s))
        throw new IllegalArgumentException(
          s"Illegal tag name: $s is not a valid XML tag name"
        )
      makeAbstractTypedTag[T](s, false)
    }
    /**
     * Converts the string to a void [[HtmlTag]]; that means that they cannot
     * contain any content, and can be rendered as self-closing tags.
     */
    def voidTag[T <: Base] = {
      if (!Escaping.validTag(s))
        throw new IllegalArgumentException(
          s"Illegal tag name: $s is not a valid XML tag name"
        )
      makeAbstractTypedTag[T](s, true)
    }
    /**
     * Converts the string to a [[UntypedAttr]]
     */
    def attr = {
      if (!Escaping.validAttrName(s))
        throw new IllegalArgumentException(
          s"Illegal attribute name: $s is not a valid XML attribute name"
        )
      Attr(s)
    }
    /**
     * Converts the string to a [[TypedAttr]]
     */
    def attrTyped[T] = {
      if (!Escaping.validAttrName(s))
        throw new IllegalArgumentException(
          s"Illegal attribute name: $s is not a valid XML attribute name"
        )
      Attr(s)
    }
    /**
     * Converts the string to a [[Style]]. The string is used as the cssName of the
     * style, and the jsName of the style is generated by converted the dashes
     * to camelcase.
     */
    def style = Style(camelCase(s), s)

  }


  /**
   * Allows you to modify a [[HtmlTag]] by adding a Seq containing other nest-able
   * objects to its list of children.
   */
  implicit class SeqModifier[A <% Modifier[Target]](xs: Seq[A]) extends Modifier[Target]{
    def applyTo(t: Target) = xs.foreach(_.applyTo(t))
  }

  /**
   * Allows you to modify a [[HtmlTag]] by adding an Option containing other nest-able
   * objects to its list of children.
   */
  implicit def OptionModifier[A <% Modifier[Target]](xs: Option[A]) = new SeqModifier(xs.toSeq)

  /**
   * Allows you to modify a [[HtmlTag]] by adding an Array containing other nest-able
   * objects to its list of children.
   */
  implicit def ArrayModifier[A <% Modifier[Target]](xs: Array[A]) = new SeqModifier[A](xs.toSeq)

  /**
   * Lets you put Unit into a scalatags tree, as a no-op.
   */
  implicit def UnitModifier(u: Unit) = new Modifier[Target]{
    def applyTo(t: Target) = ()
  }
}
